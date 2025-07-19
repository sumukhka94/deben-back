import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Checkbox } from "@/components/ui/checkbox"
import { Link } from "react-router-dom"
import { useEffect, useState } from "react"
import { Plus, Trash2 } from "lucide-react"
import axios from "axios";

export interface Member {
  id: number;
  name: string;
  avatar: string;
}

export interface GroupSummary {
  id: number;
  name: string;
  description: string;
  members: Member[];
  totalExpenses: number;
  expenseCount: number;
}

interface User {
  id: number;
  name: string;
  email: string;
}

export default function HomePage() {
  const [groups, setGroups] = useState<GroupSummary[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false);
  const [groupName, setGroupName] = useState("");
  const [groupDescription, setGroupDescription] = useState("");
  const [selectedMembers, setSelectedMembers] = useState<number[]>([]);

  useEffect(() => {
    fetchGroups();
    fetchUsers();
  }, []);

  const fetchGroups = () => {
    axios.get("http://localhost:8080/home")
      .then((response) => setGroups(response.data))
      .catch((error) => console.error("Error fetching groups", error));
  };

  const fetchUsers = () => {
    axios.get("http://localhost:8080/api/users")
      .then((response) => setUsers(response.data))
      .catch((error) => console.error("Error fetching users", error));
  };

  const handleAddGroup = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!groupName || !groupDescription || selectedMembers.length === 0) return;

    const payload = {
      name: groupName,
      description: groupDescription,
      createdBy: selectedMembers[0], // Assuming first selected member is creator
      memberIds: selectedMembers
    };

    try {
      await axios.post("http://localhost:8080/api/groups", payload);
      setIsAddDialogOpen(false);
      setGroupName("");
      setGroupDescription("");
      setSelectedMembers([]);
      fetchGroups();
    } catch (error) {
      console.error("Error creating group", error);
    }
  };

  const handleDeleteGroup = async (groupId: number, e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    
    if (confirm("Are you sure you want to delete this group?")) {
      try {
        await axios.delete(`http://localhost:8080/api/groups/${groupId}`);
        fetchGroups();
      } catch (error) {
        console.error("Error deleting group", error);
      }
    }
  };

  const toggleMember = (userId: number) => {
    setSelectedMembers(prev => 
      prev.includes(userId) 
        ? prev.filter(id => id !== userId)
        : [...prev, userId]
    );
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation */}
      <nav className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <div className="flex-shrink-0 flex items-center">
                <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
                  <span className="text-white font-bold text-sm">D</span>
                </div>
                <span className="ml-2 text-xl font-semibold text-gray-900">deben</span>
              </div>
            </div>
            <div className="flex items-center">
              <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
                <DialogTrigger asChild>
                  <Button>
                    <Plus className="w-4 h-4 mr-2" />
                    Add Group
                  </Button>
                </DialogTrigger>
                <DialogContent>
                  <DialogHeader>
                    <DialogTitle>Create New Group</DialogTitle>
                  </DialogHeader>
                  <form onSubmit={handleAddGroup} className="space-y-4">
                    <div>
                      <Label htmlFor="name">Group Name</Label>
                      <Input
                        id="name"
                        value={groupName}
                        onChange={(e) => setGroupName(e.target.value)}
                        required
                      />
                    </div>
                    <div>
                      <Label htmlFor="description">Description</Label>
                      <Textarea
                        id="description"
                        value={groupDescription}
                        onChange={(e) => setGroupDescription(e.target.value)}
                        required
                      />
                    </div>
                    <div>
                      <Label>Select Members</Label>
                      <div className="space-y-2 max-h-40 overflow-y-auto">
                        {users.map((user) => (
                          <div key={user.id} className="flex items-center space-x-2">
                            <Checkbox
                              id={`user-${user.id}`}
                              checked={selectedMembers.includes(user.id)}
                              onCheckedChange={() => toggleMember(user.id)}
                            />
                            <Label htmlFor={`user-${user.id}`} className="text-sm">
                              {user.name} ({user.email})
                            </Label>
                          </div>
                        ))}
                      </div>
                    </div>
                    <Button type="submit" className="w-full">
                      Create Group
                    </Button>
                  </form>
                </DialogContent>
              </Dialog>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Your Groups</h1>
          <p className="mt-2 text-gray-600">Manage and track expenses across different groups</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {groups.map((group) => (
            <div key={group.id} className="relative">
              <Link to={`/group/${group.id}`}>
                <Card className="hover:shadow-lg transition-shadow cursor-pointer">
                  <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                      {group.name}
                      <Badge variant="secondary">{group.expenseCount} expenses</Badge>
                    </CardTitle>
                    <CardDescription>{group.description}</CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-4">
                      <div className="flex items-center justify-between">
                        <span className="text-sm font-medium text-gray-500">Total Expenses</span>
                        <span className="text-lg font-semibold text-green-600">${group.totalExpenses.toFixed(2)}</span>
                      </div>

                      <div>
                        <span className="text-sm font-medium text-gray-500 block mb-2">Members</span>
                        <div className="flex -space-x-2">
                          {group.members.slice(0, 4).map((member) => (
                            <Avatar key={member.id} className="w-8 h-8 border-2 border-white">
                              <AvatarImage src={member.avatar || "/placeholder.svg"} alt={member.name} />
                              <AvatarFallback>
                                {member.name
                                  .split(" ")
                                  .map((n) => n[0])
                                  .join("")}
                              </AvatarFallback>
                            </Avatar>
                          ))}
                          {group.members.length > 4 && (
                            <div className="w-8 h-8 rounded-full bg-gray-200 border-2 border-white flex items-center justify-center">
                              <span className="text-xs font-medium text-gray-600">+{group.members.length - 4}</span>
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </Link>
              <Button
                variant="destructive"
                size="sm"
                className="absolute top-2 right-2 w-8 h-8 p-0"
                onClick={(e) => handleDeleteGroup(group.id, e)}
              >
                <Trash2 className="w-4 h-4" />
              </Button>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}